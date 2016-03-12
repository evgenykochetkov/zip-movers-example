# zip-movers-example

A small example demonstrating usage of [zip-movers](https://github.com/evgenykochetkov/zip-movers)

### [Live demo](http://evgenykochetkov.github.io/zip-movers-example)

### Running

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser.
Once Figwheel starts up, you should be able to open the `public/index.html` page in the browser and see the changes refresh live.


### Building

```
lein clean
lein with-profile prod cljsbuild once
```
