def lines = 1000
def tag = 'NetworkManager'

println 'launch'
def p = new ProcessBuilder()
        .command("journalctl", "--no-pager", "--lines=${lines}", "--identifier=${tag}")
        .redirectErrorStream(true)
        .start()
println 'read output'
def reader = new BufferedReader(new InputStreamReader(p.getInputStream()))
def line
while (line = reader.readLine()) {
    println line
}
println 'wait for'
p.waitFor()
println 'the end'
