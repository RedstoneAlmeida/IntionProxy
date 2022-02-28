var name = "Teste";
var version = "0.0.1";

function onEnable()
{
    manager.createCommand("test", "commandTest");
    manager.createTask("repeat", 5, true); // loop
}

function repeat(task)
{
    task.cancel();
    print("I am loop")
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


function getName() {
    return this.name;
}

function getVersion() {
    return this.version;
}