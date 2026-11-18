import java.util.List;

public interface ClippingAlgorithm {

    List<Segment> clip(
            Segment segment,
            List<Point> polygon
    );

    String getName();
}