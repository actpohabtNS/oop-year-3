package classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Hotel {
    private Integer stars;
    private Integer persons;
    private Boolean payedBreakfast;
    private Boolean hasPool;
}
