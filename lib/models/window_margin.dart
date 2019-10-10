class WindowMargin {
  int left;
  int right;
  int top;
  int bottom;

  WindowMargin({this.left, this.right, this.top, this.bottom});

  Map<String, int> getMap() {
    final Map<String, int> map = <String, int>{
      'left': left ?? 0,
      'right': right ?? 0,
      'top': top ?? 0,
      'bottom': bottom ?? 0,
    };
    return map;
  }

  static WindowMargin setSymmetricMargin(int vertical, int horizontal) {
    return WindowMargin(left: horizontal, right: horizontal, top: vertical, bottom: vertical);
  }
}
