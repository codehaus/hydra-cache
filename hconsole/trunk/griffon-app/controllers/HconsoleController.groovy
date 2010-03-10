class HconsoleController {
  def model
  def view

  void mvcGroupInit(Map args) {
    // this method is called after model and view are injected
  }

  def quit = {evt = null ->
    app.shutdown()
  }
}