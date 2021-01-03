package to.parking.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import to.parking.fixture.ParkingDataFixture;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ParkingData Tests")
public class ParkingDataTest {

    @Test
    @DisplayName("Can read a csv file successfully")
    void canReadACsvFileSuccessfully() throws Exception {

        var fixture = ParkingDataFixture.standard();

        var parkingSpotStream = fixture.getParkingData().read();

        assertThat(parkingSpotStream)
            .containsExactlyElementsOf(fixture.getParkingSpots());
    }
}
