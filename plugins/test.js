var name = "Teste";
var version = "0.0.1";

var config = manager.createConfig("test.properties");

function onEnable()
{
    manager.createCommand("test", "commandTest");
    config.put("teste", "valor");
    config.save();
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