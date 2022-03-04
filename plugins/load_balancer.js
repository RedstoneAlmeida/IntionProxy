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
    var session = event.getSession();
    print(serverName);
    if (serverName.startsWith("Lobby"))
    {
        lobbySessions[session.getServerId()] = session;
    }
}

function SessionDisconnectEvent(event)
{
    var session = event.getSession();
    if (lobbySessions[session.getServerId()] != null)
    {
        lobbySessions.splice(session.getServerId(), 1);
    }
}

function PlayerConnectEvent(event)
{
    var session = event.getSession();
    var player = event.getPlayer();
    if (transferedPlayers[player.getXuid()] != null)
        return;
    if (lobbySessions[session.getServerId()] != null)
    {
        var breaked = false;
        lobbySessions.forEach(function (server) {
            if (breaked)
                return;
            if (server.getPlayerCount() < media)
            {
                transferedPlayers[player.getXuid()] = player;
                player.transfer(server.getServerAddress(), server.getSessionName());
                breaked = true;
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