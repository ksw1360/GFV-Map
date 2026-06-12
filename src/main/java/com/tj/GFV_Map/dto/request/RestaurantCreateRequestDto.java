import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantCreateRequestDto {
    private String name;
    private String address;
    private String category;
    private Double latitude;
    private Double longitude;
    private String phone;
}