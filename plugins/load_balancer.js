var name = "LoadBalancer";
var version = "0.0.1";

var lobbySessions = [];
var transferedPlayers = [];

var media = 20;

function onEnable()
{

}

function SessionCreateEvent(event)
{
    // event.getSessionId() return SessionID
    // event.getSession() return Session class
}

function SessionInitializeEvent(event)
{
    var serverName = event.getServerName();
    print(serverName);
    if (serverName.startsWith("Lobby"))
    {
        lobbySessions[event.getSession().getServerId()] = event.getSession();
    }
    print(lobbySessions);
}

function PlayerConnectEvent(event)
{
    var session = event.getSession();
    var player = event.getPlayer();
    if (transferedPlayers[player.getXuid()] != null)
        return;
    if (lobbySessions[session.getServerId()] != null)
    {
        lobbySessions.forEach(function (server) {
            if (server.getPlayerCount() < media)
            {
                transferedPlayers[player.getXuid()] = player;
                player.transfer(server.getServerAddress(), server.getSessionName());
                return;
            }
        });
    }
}

function getName() {
    return this.name;
}

function getVersion() {
    return this.version;
}