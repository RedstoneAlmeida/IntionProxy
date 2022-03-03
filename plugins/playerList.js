var name = "PlayerList";
var version = "0.0.2";

function onEnable()
{
    manager.createCommand("playerlist", "onPlayerList");
}

function onPlayerList(values)
{
    var sessions = manager.getPlugin().getSessions().values();
    sessions.forEach(function (session)
    {
        var players = session.getPlayers().values();
        var info = "";
        players.forEach(function (player)
        {
            info += player.getUsername() + " | ";
        });
        if (info === "")
            print(session.getSessionName() + " - Empty");
        else
            print(session.getSessionName() + " - " + info)
    })
}


function getName() {
    return this.name;
}

function getVersion() {
    return this.version;
}