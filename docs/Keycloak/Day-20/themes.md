
# Keycloakify - Reference

```bash
git clone https://github.com/keycloakify/keycloakify-starter #For React
cd keycloakify-starter
npm install # Since not using yarn make sure to delete the yarn.lock
```

# How to customize the theme

```
npx keycloakify add-story # Select login.ftl (for example).
```

```
npm run storybook
```

[Documentation](https://docs.keycloakify.dev/css-customization)  

#### src/login/KcPage.tsx  

```
import "./main.css" # a custom css file imported in KcPage.tsx
```

### Define custom theme

#### vite-config.json  
```
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { keycloakify } from "keycloakify/vite-plugin";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        react(),     
        keycloakify({
            themeName: ["sparrow", "parrot"], #single theme themeName: ["sparrow"],
            accountThemeImplementation: "none"
        })
    ]
```



### Logo change  

```
npx keycloakify eject-page #Select Template.tsx
```

```
src/login/
 ├── Template.tsx
 ├── KcPage.tsx
 ├── pages/
```


#### src/login/Template.tsx  

```
import logoSvgUrl from "../assets/logo.png"; #image contains logo and realm name
//...

return (
        <div className={kcClsx("kcLoginClass")}>
            <div id="kc-header" className={kcClsx("kcHeaderClass")}>
                <div id="kc-header-wrapper" className={kcClsx("kcHeaderWrapperClass")}>
                   <img src={logoSvgUrl} width={500}/> #added line
                </div>
            </div>

```

### Testing in Keycloak     

```
npx keycloakify start-keycloak #Live update of themes supported. Prompts for Keycloak version selection & jar file applicable to it is built
```

#### Production build  

```
npm run build-keycloak-theme
```

Two jars will be generated under keycloak-starter/dist_keycloak. One support Keycloak 22 to 25 and the other 11 to 21 and 26 and beyond

### Multiple themes


#### keycloak-starter/vite.config,ts  

```
export default defineConfig({
    plugins: [
        react(),     
        keycloakify({
            themeName: ["parrot", "sparrow"],
            accountThemeImplementation: "none"
        })
    ]
});
```

#### src/login/Template.tsx  

```
//............
import logoParrotUrl from "../assets/logo.png";
import logoSparrowUrl from "../assets/logo512.png";
//..................
<img src={(() => {
                    switch (kcContext.themeName) {
                        case "parrot":
                            return logoParrotUrl;
                        case "sparrow":
                            return logoSparrowUrl;
                    }
                })()}
                    width={500}
                />
```

Reference: https://docs.keycloakify.dev/














