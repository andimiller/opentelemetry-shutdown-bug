import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ShutdownProblems {
    public static void main(String[] args) {
        var logger = LoggerFactory.getLogger("main");
        var exporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317")
                .build();

        var processor = BatchSpanProcessor.builder(exporter).build();
        var tracer = SdkTracerProvider.builder()
                .addSpanProcessor(processor)
                .build();
        var prop = ContextPropagators.create(W3CTraceContextPropagator.getInstance());

        var ot = OpenTelemetrySdk.builder().setTracerProvider(tracer).setPropagators(prop).build();

        logger.info("configured");
        var tracerProvider = ot.getTracerProvider().get("fake.instrumentation");
        var span = tracerProvider.spanBuilder("my span").startSpan();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        span.end();
        logger.info("did a span");

        // and then shut everything down
        logger.info("shutting down tracer");
        tracer.shutdown().join(10, TimeUnit.MINUTES);
        logger.info("shutting down processor");
        processor.shutdown().join(10, TimeUnit.MINUTES);
        logger.info("shutting down exporter");
        exporter.shutdown().join(10, TimeUnit.MINUTES); // this hangs for the whole time
        logger.info("done");
    }
}
