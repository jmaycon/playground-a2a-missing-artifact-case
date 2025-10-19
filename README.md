# playground-a2a-missing-artifact-case

Demonstrated multi-agent with distributed reasoning 

## Starting

Proxy & Otel

```shell
docker compose up -d
```

## Ollama

Download and install olama

### Installing Reasoning Model

Supports reasoning, but it’s best viewed as a balanced 
instruction-following model — solid for tool orchestration, 
structured generation, and short reasoning chains

```shell
ollama pull llama3.1:8b-instruct-q4_K_M
```


#### Testing with curl

```shell
curl -X POST http://localhost:11434/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama3.1:8b-instruct-q4_K_M",
    "prompt": "Say hello in one sentence."
  }'
```

```shell
 curl -v POST http://localhost:11434/api/generate \
    -H "Content-Type: application/json" \
    -d '{"model":"llama3.1:8b-instruct-q4_K_M","prompt":"Say hi"}'
```

## Debugging MCP

This command launches a debugging UI that allows you to validate available tools and resources.  
You can use it to interact directly with the tools exposed by your MCP server and verify their behavior.


```shell
npx @modelcontextprotocol/inspector --transport sse http://localhost:30010/mcp/message
```


## Using with Claude Desktop

To use your service with **Claude Desktop**, install the [`mcp-proxy`](https://www.npmjs.com/package/mcp-remote) package.  
The proxy runs through Claude Desktop and enables it to call your service.

After installation, configure the proxy in the `claude_desktop_config.json` file.  
You can find the file’s location from **Claude Desktop → Settings → Advanced → Config File Path**.

For more details, see the [mcp-remote documentation on npm](https://www.npmjs.com/package/mcp-remote).


```json
{
  "mcpServers": {
    "detective_sherlock": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:30010/sse",
        "--allow-http"
      ]
    },
    "investigator_watson": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:30011/sse",
        "--allow-http"
      ]
    },
    "museum_staff": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:30012/sse",
        "--allow-http"
      ]
    },
    "security": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:30013/sse",
        "--allow-http"
      ]
    }
  }
}
```

## Call Inspection see

http://localhost:8081/, pass=`test`

## For the gateway 

Access http://localhost:4444/

Installation required for Claude desktop

```shell
 # Ensure pip can overwrite files
>> C:\Python312\python.exe -m pip install --upgrade pip setuptools wheel
>> C:\Python312\python.exe -m pip install --force-reinstall --upgrade mcp-contextforge-gateway
```

## See is deprecated see Streamable HTTP not implemented yet
- See https://github.com/spring-projects/spring-ai/issues/3145
- See also a gist for https://gist.github.com/nagavijays/7439c331ffb6140ddc619b6b334c7564