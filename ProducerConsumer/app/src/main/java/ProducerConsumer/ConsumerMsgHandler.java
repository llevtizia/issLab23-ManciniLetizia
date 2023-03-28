package ProducerConsumer;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMsgHandler;
import unibo.basicomm23.utils.CommUtils;

/*
Gestore dei messaggi rivolti al consumer
 */
public class ConsumerMsgHandler extends ApplMsgHandler {
private ConsumerLogic consunerLogic;

    public ConsumerMsgHandler(String name, ConsumerLogic consunerLogic ) {
        super(name);
        this.consunerLogic = consunerLogic;
    }

    @Override
    public void elaborate(IApplMessage message, Interaction conn) {
        try {
            CommUtils.outgreen(name + " elaborate " + message );
            String d = message.msgContent();
            String m = consunerLogic.evalDistance( d ) ;
            //CommUtils.outgreen(m);
            // dovrei fare un check sul tipo di messaggio ricevuto (verificare sia una request)
            IApplMessage reply = CommUtils.buildReply("consumer", "outdata", m, message.msgSender());
            // msg.Sender() = destinatario --> reply a chi aveva mandato la request
            // m = payload
            conn.reply( reply );
        } catch (Exception e) {
             e.printStackTrace();
        }
    }
}