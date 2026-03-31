package csd.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

/**
 * Netty WebSocket Server for Agent Gateway.
 * [INTENT] Entry point for control plane protocol.
 */
public class GatewayServer {

    private static final Logger log = LoggerFactory.getLogger(GatewayServer.class);
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public GatewayServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        // HTTP codec for WebSocket handshake
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        // WebSocket compression
                        pipeline.addLast(new WebSocketServerCompressionHandler());
                        // WebSocket protocol handler
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true));
                        // Our gateway handler
                        pipeline.addLast(new GatewayHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // [BACKPRESSURE] Write buffer water marks (low=8KB, high=32KB)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, 
                    new WriteBufferWaterMark(8 * 1024, 32 * 1024));

            kvlog("server_start", "port=" + port, "transport=ws", "path=/ws");
            
            ChannelFuture future = bootstrap.bind(port).sync();
            kvlog("server_listen", "host=0.0.0.0", "port=" + port);
            
            future.channel().closeFuture().sync();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        kvlog("server_stop", "port=" + port);
    }

    private void kvlog(String event, String... fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("ts=").append(Instant.now()).append(" event=").append(event);
        for (String field : fields) {
            sb.append(" ").append(field);
        }
        log.info(sb.toString());
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8765;
        new GatewayServer(port).start();
    }
}
