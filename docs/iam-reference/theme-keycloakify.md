
# Keycloakify - Quick start

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

####src/login/KcPage.tsx

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
});
```

### Define background

#### src/login/main.css

```

body.kcBodyClass {
  background: url(./assets/background.jpg) no-repeat center center; #assets in 
}
```
#### For all pages - change background

```
npx keycloakify eject-page #Select Template.tsx
```

```
src/login/
 ├── Template.tsx
 ├── KcPage.tsx
 ├── pages/
 ```




### Changing the background

```
```




