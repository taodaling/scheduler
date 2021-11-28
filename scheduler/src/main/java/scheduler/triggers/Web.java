package scheduler.triggers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import scheduler.context.Context;
import utils.ErrorUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Web extends AbstractTrigger {
    @Override
    public void run() {
        int port = Integer.parseInt(context.get("port"));
        Server server = new Server(port);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
                int length = req.getContentLength();
                String input = new String(req.getInputStream().readNBytes(length), StandardCharsets.UTF_8);
                Map<String, String> env = new HashMap<>();
                env.put("_request_ip", req.getRemoteAddr());
                env.put("_request_body", input);
                res.setCharacterEncoding("utf8");
                PrintWriter writer = res.getWriter();
                writer.println("{\"code\": \"ok\"}");
                writer.close();

                try {
                    execute(Context.inherit(context, env), Web.this);
                } catch (Exception e) {
                    ErrorUtils.throwAsRuntimeException(e);
                }
            }
        });
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            ErrorUtils.throwAsRuntimeException(e);
        }
    }
}
