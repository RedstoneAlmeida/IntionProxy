var name = "Teste";
var version = "0.0.1";

function onEnable()
{
    manager.createCommand("test", "commandTest");
}

function SessionCreateEvent(event)
{
    print(event.getSessionId());
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