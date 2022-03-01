var name = "Teste";
var version = "0.0.1";

function onEnable()
{
    manager.createCommand("test", "commandTest");
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