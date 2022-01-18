function getChildFrames() {
    let arr = [];
    let iFrames = document.getElementsByTagName("iframe");
    for (let i = 0; i < iFrames.length; i++) {
        arr.push(iFrames[i].src);
    }
    return arr;
}