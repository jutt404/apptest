# SaaS Server Android App - FULL FIXED SOURCE

This is the complete Android WebView app source for your panel.

Main user panel URL:

`https://wzdev.qzz.io/index.php`

Admin URL is hidden by default:

`https://wzdev.qzz.io/ad-admin/`

## Important files included

- `app/src/main/java/com/wzdev/saasserver/MainActivity.java`
- `app/src/main/java/com/wzdev/saasserver/AppConfig.java`
- `app/src/main/AndroidManifest.xml`
- `app/build.gradle`
- `build.gradle`
- `settings.gradle`
- `gradle.properties`
- `codemagic.yaml`
- `.github/workflows/build-apk.yml`
- `gradlew` and `gradlew.bat`

## Upload to GitHub

Upload everything inside this folder to the root of your GitHub repo.

Correct GitHub root should look like:

```text
app/
build.gradle
settings.gradle
gradle.properties
gradlew
gradlew.bat
codemagic.yaml
README.md
.github/
```

Do not upload only the `app` folder. Upload all files.

## Build on Codemagic

1. Open Codemagic.
2. Add application or open your current application.
3. Click Finish build setup.
4. Select YAML / codemagic.yaml.
5. Choose workflow: `Build SaaS Server Debug APK`.
6. Start build.
7. Download APK from Artifacts.

## Customize

Open:

`app/src/main/java/com/wzdev/saasserver/AppConfig.java`

Change these values:

- `HOME_URL`
- `SUPPORT_URL`
- `ALLOWED_HOSTS`
- `SHOW_ADMIN_SHORTCUT`
- App colors

Change app name:

`app/src/main/res/values/strings.xml`

Change package/application ID:

`app/build.gradle`

Current application ID:

`com.wzdev.saasserver`

## Permissions

Only safe permissions are used:

- `INTERNET`
- `ACCESS_NETWORK_STATE`
