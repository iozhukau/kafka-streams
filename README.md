### Useful links

* [Иван Пономарёв, КУРС - Kafka Streams API: шаг за рамки Hello World. Part 1](https://www.youtube.com/watch?v=pipM6bwQjoM)
* [Иван Пономарёв, КУРС - Kafka Streams API: шаг за рамки Hello World. Part 2](https://www.youtube.com/watch?v=PqQax9zur9I)
* [Stephane Marek, Create a KSQL Stream Tutorial](https://www.youtube.com/watch?v=3gj_CHlVkNE)
* [Виктор Гамов, Барух Садогурский — Боремся с Russian Hackers с помощью Kafka Streams и Firehose API](https://www.youtube.com/watch?v=ZH3AlesuSpw)


* [ksqlDB Quickstart](https://ksqldb.io/quickstart.html)
* [ksqlDB Quickstart by Confluent](https://docs.confluent.io/5.0.4/ksql/docs/quickstart.html)


* [Kafka Streams Guide](https://kafka.apache.org/21/documentation/streams/developer-guide/config-streams.html)
* [Kafka Stream DSL](https://kafka.apache.org/20/documentation/streams/developer-guide/dsl-api.html)


* [Визуализатор топологии стримов](https://zz85.github.io/kafka-streams-viz/)

### Основы

**KSQL** — это механизм потокового SQL для Apache Kafka®.
С помощью KSQL вы можете писать приложения для потоковой передачи в реальном времени,
используя язык запросов, подобный SQL.

**Kafka Streams** — это библиотека Apache Kafka® для написания потоковых приложений и микросервисов на Java и Scala.


<img src="https://docs.confluent.io/5.0.4/_images/ksql-kafka-streams-core-kafka-stack.png" alt="Image by https://docs.confluent.io/" style="width:800px;"/>

### Пример кода

**kSQL**
```(sql)
CREATE STREAM fraudulent_payments AS
SELECT fraudProbability(data) FROM payments
WHERE fraudProbability(data) > 0.8;
```

**Scala**
```(java)
// Example fraud-detection logic using the Kafka Streams API.
object FraudFilteringApplication extends App {

    val builder: StreamsBuilder = new StreamsBuilder()
    
    val fraudulentPayments: KStream[String, Payment] 
        = builder
            .stream[String, Payment]("payments-kafka-topic")
            .filter((_ ,payment) => payment.fraudProbability > 0.8)
                                                            
    fraudulentPayments.to("fraudulent-payments-topic")

    val config = new java.util.Properties
    
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "fraud-filtering-app")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker1:9092")

    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
    
    streams.start()
}
```

### Отличия подхода

**KSQL**

Вы пишете запросы KSQL в интерактивном режиме и просматриваете результаты в режиме реального времени
либо в интерфейсе командной строки KSQL, либо в Confluent Control Center.
Вы можете сохранить файл .sql и развернуть его в рабочей среде как «безголовое» приложение,
которое работает без графического интерфейса, интерфейса командной строки или интерфейса REST на серверах KSQL.

**Кафка Потоки**

Вы пишете код на Java или Scala, перекомпилируете, запускаете и тестируете приложение в среде IDE,
например IntelliJ. Вы развертываете приложение в рабочей среде в виде JAR-файла,
который запускается в кластере Kafka.


