import {
  Panel,
  Group,
  vlayout,
  layoutConfig,
  Gravity,
  text,
  Text,
  Color,
  navbar,
  modal,
} from "doric";
import { fs as fsBridge } from "doric-fs";

const fs = fsBridge(context);

@Entry
class DoricFs extends Panel {
  onShow() {
    navbar(context).setTitle("DoricFs");
    (async () => {
      const documentPath = await fs.getDocumentsDir();
      const files = await fs.readDir(documentPath);
      modal(context).alert(JSON.stringify(files));
    })().then();
  }
  build(rootView: Group): void {
    let number: Text;
    let count = 0;
    vlayout([
      (number = text({
        textSize: 40,
        text: "0",
      })),
      text({
        text: "Click to count",
        textSize: 20,
        backgroundColor: Color.parse("#70a1ff"),
        textColor: Color.WHITE,
        onClick: async () => {
          number.text = `${++count}`;
          const documentPath = await fs.choose({
            uniformTypeIdentifiers: ["public.folder"],
            mimeType: ""
          })
          modal(context).alert(documentPath);
        },
        layoutConfig: layoutConfig().just(),
        width: 200,
        height: 50,
      }),
    ])
      .apply({
        layoutConfig: layoutConfig().just().configAlignment(Gravity.Center),
        width: 200,
        height: 200,
        space: 20,
        border: {
          color: Color.BLUE,
          width: 1,
        },
        gravity: Gravity.Center,
      })
      .in(rootView);
  }
}
