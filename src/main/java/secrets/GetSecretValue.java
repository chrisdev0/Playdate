package secrets;

import java.util.Optional;

public interface GetSecretValue {
    Optional<String> getValue(String key);
}
