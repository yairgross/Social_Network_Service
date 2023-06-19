package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;
import bgu.spl.net.api.bidi.BGSProtocol;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        int numberOfThreads = Integer.parseInt(args[1]);
        int port = Integer.parseInt(args[0]);
        Server.reactor(
                numberOfThreads,
                port,
                () -> new BGSProtocol(),
                () -> new BGSEncoderDecoder()
        ).serve();
    }
}
