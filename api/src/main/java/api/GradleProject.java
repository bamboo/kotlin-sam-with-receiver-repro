package api;

import java.util.ArrayList;
import java.util.List;

public interface GradleProject {

    CopySpec copySpec(Action<? super CopySpec> configuration);

    class CopySpec {

        private final List<Object> inputs = new ArrayList<>();

        private Object target;

        public void from(Object source) { inputs.add(source); }

        public void into(Object target) { this.target = target; }

        @Override
        public String toString() {
            return "CopySpec{" +
                    "inputs=" + inputs +
                    ", target=" + target +
                    '}';
        }
    }
}
