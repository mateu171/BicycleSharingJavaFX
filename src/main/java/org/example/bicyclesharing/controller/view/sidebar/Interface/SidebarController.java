package org.example.bicyclesharing.controller.view.sidebar.Interface;

import org.example.bicyclesharing.controller.window.MainMenuController;
import org.example.bicyclesharing.viewModel.MainMenuViewModel;

public interface SidebarController {

  void setMainMenuController(MainMenuController controller, MainMenuViewModel viewModel);
}
