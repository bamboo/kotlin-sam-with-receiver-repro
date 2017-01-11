package api;

public class DefaultGradleProject implements GradleProject {

    @Override
    public CopySpec copySpec(Action<? super CopySpec> configuration) {
        final CopySpec spec = new CopySpec();
        configuration.execute(spec);
        return spec;
    }
}
