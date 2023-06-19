package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;
import bgu.spl.net.api.bidi.BGSProtocol;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        Server.threadPerClient(
                port,
                () -> new BGSProtocol(),
                () -> new BGSEncoderDecoder()
        ).serve();
    }
}
