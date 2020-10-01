package terrails.statskeeper.feature;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;

public abstract class Feature {

    public abstract String name();

    public abstract void setupConfig(final ConfigTreeBuilder tree);

    /**
     * @return if {@link #setupConfig(ConfigTreeBuilder)} should be executed on config load
     */
    public boolean canLoadConfig() {
        return true;
    }

    /**
     * @return if a certain mod is loaded or something
     */
    public boolean canLoad() {
        return true;
    }

    public abstract void initializeEvents();

    protected  <R> ConfigTreeBuilder configValue(ConfigTreeBuilder parent, String name, PropertyMirror<R> mirror, R defaultValue, String comment) {
        parent.beginValue(name, mirror.getMirroredType(), defaultValue).withComment(comment).finishValue(mirror::mirror);
        return parent;
    }

    protected <R> ConfigTreeBuilder configValue(ConfigTreeBuilder parent, String name, PropertyMirror<R> mirror, R defaultValue) {
        parent.beginValue(name, mirror.getMirroredType(), defaultValue).finishValue(mirror::mirror);
        return parent;
    }
}
