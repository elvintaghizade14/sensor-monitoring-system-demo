package az.et.warehouse.config;

import static az.et.warehouse.model.constants.ApplicationConstant.CHANNEL_DEBUG;
import static az.et.warehouse.model.constants.ApplicationConstant.CHANNEL_HUMIDITY;
import static az.et.warehouse.model.constants.ApplicationConstant.CHANNEL_TEMPERATURE;

import az.et.warehouse.config.properties.WarehouseProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.MessageChannel;

@Configuration
@RequiredArgsConstructor
public class UdpIntegrationConfig {

    private final WarehouseProperties properties;
    private final AsyncTaskExecutor virtualThreadExecutor;

    @Bean(name = CHANNEL_TEMPERATURE)
    public MessageChannel temperatureChannel() {
        return new ExecutorChannel(virtualThreadExecutor);
    }

    @Bean
    public UnicastReceivingChannelAdapter temperatureAdapter() {
        return createAdapter(properties.getTemperaturePort(), CHANNEL_TEMPERATURE);
    }

    @Bean(name = CHANNEL_HUMIDITY)
    public MessageChannel humidityChannel() {
        return new ExecutorChannel(virtualThreadExecutor);
    }

    @Bean
    public UnicastReceivingChannelAdapter humidityAdapter() {
        return createAdapter(properties.getHumidityPort(), CHANNEL_HUMIDITY);
    }

    @Bean(name = CHANNEL_DEBUG)
    public MessageChannel debugChannel() {
        return new ExecutorChannel(virtualThreadExecutor);
    }

    @Bean
    public UnicastReceivingChannelAdapter debugAdapter() {
        return createAdapter(properties.getDebugPort(), CHANNEL_DEBUG);
    }

    private UnicastReceivingChannelAdapter createAdapter(int port, String channelName) {
        UnicastReceivingChannelAdapter adapter = new UnicastReceivingChannelAdapter(port);
        adapter.setOutputChannelName(channelName);
        return adapter;
    }
}
