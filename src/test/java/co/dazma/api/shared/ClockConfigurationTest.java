package co.dazma.api.shared;


import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClockConfigurationTest {

    @Test
    void mustGetClockInUTC()
    {
        ClockConfiguration clockConfiguration = new ClockConfiguration();
        Clock clock = clockConfiguration.clock();
        assertEquals(ZoneOffset.UTC, clock.getZone());
    }

}
