

build-dev:
	docker build --tag=simple-ml-frontend .

run:
	docker run -v ${PWD}:/app -v /app/node_modules -p 4200:4200 --rm simple-ml-frontend
