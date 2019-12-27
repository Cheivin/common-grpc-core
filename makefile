.DEFAULT_GOAL := build

build:
	mvn clean protobuf:compile protobuf:compile-custom package
proto:
	mvn protobuf:compile protobuf:compile-custom
clean:
	mvn clean