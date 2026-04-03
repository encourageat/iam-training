exports.onExecutePostLogin = async (event, api) => {

  if (event.client.name === "custom-client") {
    const d = new Date().getDay();

    // 0 = Sunday, 6 = Saturday
    if (d === 0 || d === 6) {
      api.access.deny("This app is only available during weekdays");
    }
  }

};