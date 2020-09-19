import resolve from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import fs from "fs";
import path from "path";
import json from "@rollup/plugin-json";
import image from "@rollup/plugin-image";

function searchImages(dir, images) {
  const files = fs.readdirSync(dir);
  files.forEach((item, index) => {
    var fullPath = path.join(dir, item);
    const stat = fs.statSync(fullPath);
    if (stat.isDirectory()) {
      searchImages(path.join(dir, item), images);
    } else {
      if (fullPath.endsWith(".png")) {
        images.push(fullPath);
      }
    }
  });
  return images;
}

const allImages = [];
searchImages("src", allImages);

function mkdirsSync(dirname) {
  if (fs.existsSync(dirname)) {
    return true;
  } else {
    if (mkdirsSync(path.dirname(dirname))) {
      fs.mkdirSync(dirname);
      return true;
    }
  }
}

allImages.forEach((value) => {
  let path = __dirname + "/build/" + value;
  let index = path.lastIndexOf("/");
  mkdirsSync(path.substring(0, index));

  fs.copyFile(
    __dirname + "/" + value,
    __dirname + "/build/" + value,
    (error) => {
      console.log(error);
    }
  );
});
export default [
  {
    input: `build/src/DoricFs.js`,
    output: {
      format: "cjs",
      file: `bundle/src/DoricFs.js`,
      sourcemap: true,
    },
    plugins: [resolve({ mainFields: ["jsnext"] }), commonjs(), json(), image()],
    external: ["reflect-metadata", "doric"],
    onwarn: function (warning) {
      if (warning.code === "THIS_IS_UNDEFINED") {
        return;
      }
      console.warn(warning.message);
    },
  },
];
