# OverflowLimbo [![Build OverflowLimbo](https://img.shields.io/github/actions/workflow/status/CodeTheDev/OverflowLimbo/build.yml?branch=master)](https://github.com/CodeTheDev/OverflowLimbo/actions)
A Minestom-powered void limbo server.

## Compiling
Open a Terminal in a directory of your choosing. You will need `git` installed on your system.

**Clone the repository from GitHub:**
```
git clone https://github.com/CodeTheDev/OverflowLimbo.git
```
**Navigate to the cloned repository:**
```
cd OverflowLimbo
```
**Compile using the following Gradle command:**
```
./gradlew shadowJar --stacktrace
```

To find the compiled server jar, navigate to the `build/libs` directory inside the repository after running the above commands.

## Configuration (server.properties)
| Key                     | Default Value                                           | Type    | Information                                                          |
|-------------------------|---------------------------------------------------------|---------|----------------------------------------------------------------------|
| *server-brand*          | `OverflowLimbo`                                         | String  | The server brand displayed in the F3 menu.                           |
| *server-address*        | `127.0.0.1`                                             | IP      | The IP address to bind for the server.                               |
| *server-port*           | `25565`                                                 | Integer | The port of the server.                                              |
| *compression-threshold* | `-1`                                                    | Integer | Network Compression (-1 is 0, which means no compression).           |
| *view-distance*         | `3`                                                     | Integer | Player view distance (May cause visual issues if set below 3).       |
| *max-players*           | `-1`                                                    | Integer | Maximum player limit (-1 is unlimited, player limit is only visual). |
| *motd*                  | `<dark_aqua><i>An OverflowLimbo Server</i></dark_aqua>` | String  | Server list MOTD (Uses MiniMessage text formatting).                 |
| *tablist-enabled*       | `false`                                                 | Boolean | Toggle tablist visibility.                                           |
| *proxy-type*            | `none`                                                  | String  | Specifies the type of proxy being used (velocity, bungeecord, none). |
| *velocity-secret*       | ` `                                                     | String  | Velocity secret for modern forwarding.                               |
| *bungeeguard-token*     | ` `                                                     | String  | BungeeGuard token for secure BungeeCord forwarding.                  |
