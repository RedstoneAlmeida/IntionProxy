var name = "Teste";
var version = "0.0.1";

function onEnable()
{
    manager.createCommand("test", "commandTest");
}

function SessionCreateEvent(event)
{
    // event.getSessionId() return SessionID
    // event.getSession() return Session class
}

function SessionInitializeEvent(event)
{
    // event.setServerName(string) defines SessionName
    // event.getSessionName()
}

function commandTest(values)
{
    print("test work");
}

function eventCommand(command)
{
    return true;
}

function getName() {
    return this.name;
}

function getVersion() {
    return this.version;
}