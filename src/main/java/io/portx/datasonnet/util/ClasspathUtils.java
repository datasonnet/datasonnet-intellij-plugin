package io.portx.datasonnet.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.psi.util.ParameterizedCachedValueProvider;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public final class ClasspathUtils {
    private static final Key<ParameterizedCachedValue<List<URL>, Module>> URLS_KEY = Key.create("MODULE.URLS");

    private static final ClasspathUtils classpathUtils = new ClasspathUtils();

    private ClasspathUtils() {

    }

    public static ClasspathUtils getInstance() {
        return classpathUtils;
    }

    public static ClassLoader getProjectClassLoader(Project project, ClassLoader parent) throws Exception {
        ClassLoader fullClassLoader = null;

        List<URL> loaderUrls = new ArrayList<>();

        Module[] modulesList = ModuleManager.getInstance(project).getModules();
        for (Module nextModule : modulesList) {
            loaderUrls.addAll(getURLsForModule(nextModule));
        }

        fullClassLoader = new URLClassLoader(loaderUrls.toArray(new URL[]{}), parent);

        return fullClassLoader;
    }

    public static ClassLoader getModuleClassLoader(Module module, ClassLoader parent) throws Exception {
        ClassLoader moduleClassLoader = null;

        List<URL> loaderUrls = getURLsForModule(module);

        moduleClassLoader = new URLClassLoader(loaderUrls.toArray(new URL[]{}), parent);

        return moduleClassLoader;
    }

    private static List<URL> getURLsForModule(Module module) throws Exception {

        final CachedValuesManager manager = CachedValuesManager.getManager(module.getProject());
        List<URL> loaderUrls = manager.getParameterizedCachedValue(module, URLS_KEY, new UrlsCachedProvider(), false, module);

        return loaderUrls;
    }

    private static class UrlsCachedProvider implements ParameterizedCachedValueProvider<List<URL>, Module> {
        @Nullable
        @Override
        public CachedValueProvider.Result<List<URL>> compute(Module module) {
            List<URL> loaderUrls = new ArrayList<>();

            ArrayList<Object> dependencies = new ArrayList<Object>();
            dependencies.add(ProjectRootManager.getInstance(module.getProject()));
            //dependencies.add(module);

            String fullClasspath = OrderEnumerator.orderEntries(module).recursively().getPathsList().getPathsString();

            // Windows uses semicolons as the path separator and Linux uses colons. We need to split the classpath
            // based on the correct separator.
            String[] cpEntries = fullClasspath.split(File.pathSeparator);
            for (String nextEntry : cpEntries) {
                try {
                    URL url = nextEntry.endsWith(".jar") ? URI.create("jar:file://" + nextEntry + "!/").toURL() : URI.create("file://" + nextEntry).toURL();
                    loaderUrls.add(url);
                } catch (Exception e) {

                }
            }

            CompilerModuleExtension extension = CompilerModuleExtension.getInstance(module);
            String[] outputRootUrls = extension.getOutputRootUrls(false);
            for (String nextUrlString : outputRootUrls) {
                // URI does not accept backslashes. Windows paths are returned with backslashes, so we need to
                // replace them with forward slashes.
                nextUrlString = nextUrlString.replaceAll("\\\\", "/");
                if (!nextUrlString.endsWith("/")) {
                    nextUrlString = nextUrlString + "/";
                }

                try {
                    loaderUrls.add(URI.create(nextUrlString).toURL());
                } catch (Exception e) {

                }
            }

            return CachedValueProvider.Result.create(loaderUrls, dependencies);
        }
    }
}
