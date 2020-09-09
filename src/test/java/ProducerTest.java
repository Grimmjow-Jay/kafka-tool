import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * @author Grimm
 * @date 2020/9/9
 */
public class ProducerTest {

    private static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {

        String format = new SimpleDateFormat().format(new Date());
        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "192.168.154.203:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 1000; i++) {
            producer.send(new ProducerRecord<>("grimm_test_topic", "key" + i, format + "message" + i));
//            Thread.sleep(1000L + random.nextInt(5000));
        }
        producer.close();
    }
}
