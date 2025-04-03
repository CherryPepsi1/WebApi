FROM mcr.microsoft.com/dotnet/aspnet:9.0

# Set working directory
WORKDIR /usr/app

# Copy files
COPY src/bin/Debug/net9.0 /usr/app

# Run node
ENTRYPOINT ["dotnet","WebApi.dll"]