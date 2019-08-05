package bgu.spl181.net.srv;

import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import java.util.function.Supplier;

public class ThreadPerClientServer<T> extends BaseServer<T>{


    public ThreadPerClientServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encoderDecoderFactory) {

        super(port, protocolFactory, encoderDecoderFactory);
    }

    protected void execute(BlockingConnectionHandler handler) {
        new Thread(handler).start();
    }

}
