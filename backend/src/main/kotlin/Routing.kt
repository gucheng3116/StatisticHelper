package com.gucheng

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.* // 新增：用于 static/files/default
import java.io.File // 新增：用于 respondFile

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        // 静态文件：将项目根下的 public 目录作为静态资源根，这样 /privacy.html 和 /tos.html 可直接访问
        // 使用 Ktor 推荐的 staticFiles API 代替已废弃的 static { files(...) }
        staticFiles("/", File("public")) {
            default("index.html")
        }

        // 友好路径：/privacy -> public/privacy.html, /terms -> public/tos.html
        get("/privacy") {
            call.respondFile(File("public/privacy.html"))
        }
        get("/terms") {
            call.respondFile(File("public/tos.html"))
        }
    }
}
