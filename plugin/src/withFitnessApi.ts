import {
  ConfigPlugin,
  createRunOncePlugin,
  withInfoPlist,
  withSettingsGradle,
  withAppBuildGradle,
} from "@expo/config-plugins";

const pkg = require("@ovalmoney/react-native-fitness/package.json");

const HEALTH_USAGE_DESCRIPTION = "Allow $(PRODUCT_NAME) acess your health data";

type IOSPermissionProps = {
  healthShareUsageDescription?: string;
};

const withFitnessApi: ConfigPlugin<IOSPermissionProps | void> = (
  initialConfig,
  props
) => {
  const iosConfig = withInfoPlist(initialConfig, (config) => {
    const { healthShareUsageDescription } = props || {};

    config.modResults.NSHealthShareUsageDescription =
      healthShareUsageDescription ||
      config.modResults.NSHealthShareUsageDescription ||
      HEALTH_USAGE_DESCRIPTION;

    return config;
  });

  const configWithGradleSettings = withSettingsGradle(iosConfig, (config) => {
    config.modResults.contents =
      config.modResults.contents +
      "\ninclude ':@ovalmoney_react-native-fitness' \nproject(':@ovalmoney_react-native-fitness').projectDir = new File(rootProject.projectDir, 	'../node_modules/@ovalmoney/react-native-fitness/android')\n";

    return config;
  });

  const configWithGradleBuild = withAppBuildGradle(
    configWithGradleSettings,
    (config) => {
      const parts = config.modResults.contents.split("dependencies {\n");
      config.modResults.contents =
        parts[0] +
        "dependencies {\n    implementation project(':@ovalmoney_react-native-fitness')\n" +
        parts[1];

      return config;
    }
  );

  return configWithGradleSettings;
};

export default createRunOncePlugin(withFitnessApi, pkg.name, pkg.version);
