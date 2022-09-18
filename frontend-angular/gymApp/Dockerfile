# pull official base image
FROM node:16.13.1-alpine

# set working directory
WORKDIR /app

# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH

## install app dependencies
#COPY package.json package-lock.json ./
RUN npm install -g @angular/cli
#RUN npm install --silent
#COPY . /app
RUN npm install --save-dev @angular-devkit/build-angular


# start app
CMD ["ng", "serve"]
