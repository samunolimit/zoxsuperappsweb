package com.example.model

enum class PluginCategory {
  MOBILITY,
  LOGISTICS,
  COMMUNICATION,
  COMMERCE,
  SERVICES,
  MONETIZATION
}

data class AppPlugin(
  val id: String,
  val title: String,
  val subtitle: String,
  val iconName: String,
  val category: PluginCategory,
  val isInstalled: Boolean = true,
  val isEnabled: Boolean = true,
  val isNew: Boolean = false,
  val badge: String? = null,
  val version: String = "2.4.0",
  val minAppVersion: String = "1.0.0"
)
