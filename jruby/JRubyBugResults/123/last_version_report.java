@Override
public void load(Ruby runtime, boolean wrap) {
    try {
        URL url;
        if (location.startsWith(URLResource.URI)) {
            url = null;
            runtime.getJRubyClassLoader().addURLNoIndex(URLResource.getResourceURL(runtime, location));
        } else {
            File f = new File(location);
            if (f.exists() || location.contains( "!")){
                url = f.toURI().toURL();
                if (location.contains( "!")) {
                    url = new URL( "jar:" + url );
                }
            } else {
                url = new URL(location);
            }
        }
        if (url != null) {
            runtime.getJRubyClassLoader().addURL(url);
        }
    } catch (MalformedURLException badUrl) {
        runtime.newIOErrorFromException(badUrl);
    }

    // If an associated Service library exists, load it as well
    ClassExtensionLibrary serviceExtension = ClassExtensionLibrary.tryFind(runtime, searchName);
    if (serviceExtension != null) {
        serviceExtension.load(runtime, wrap);
    }
}
