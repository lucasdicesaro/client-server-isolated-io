# client-server-isolated-io


Naturally, the server handles a TCP connection with each client that connects to it.
The different thing here is that both the Client and the Server have 3 components:
- The Main process
- The Keyboard process
- The Console process

Additionally, these 3 components are communicated via sockets as well.

