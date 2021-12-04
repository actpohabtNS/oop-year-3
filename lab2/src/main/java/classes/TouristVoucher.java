package classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class TouristVoucher {
    private String country;
    private TripType type;
    private TransportType transport;
    private Integer days;
    private Hotel hotel;
    private Integer price;
}
