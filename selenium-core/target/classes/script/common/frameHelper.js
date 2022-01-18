$(function () {
    (function (obj) {


        /**
         * 私有方法,不断递归往下找iframe,直到找到想要的url所在的url,然后把路径(dom节点)放在result中
         * @param nowFrame 当前iframe
         * @param result 用来存放到达这个iframe的所有路径
         * @param url 需要查找的url
         * @returns {boolean}
         */
        function _getTheFrame(nowFrame, result, url) {
            let frames = nowFrame.frames;
            for (let i = 0, size = frames.length; i < size; i++) {
                let frame = frames[i];
                let ele = frame.frameElement;
                console.debug(ele);
                result.push(ele);
                if (ele.src.indexOf(url) !== -1 || _getTheFrame(frame, result, url)) {
                    return true;
                }
                result.splice(result.length - 1, 1);
            }
            return false;
        }

        class FrameObj {
            /**
             * 根据url查询要查找的iframe
             * @param url 需要寻找的url
             * @returns {Array} 返回到到达这个iframe的路径
             */
            static findTheFrame(url) {
                let arr = [];
                _getTheFrame(self, arr, url);
                arr.forEach(item => {
                    console.log(item.src);
                });
                return arr;
            }
        }

        obj.frameHelper = obj.frameHelper || {};
        obj.frameHelper.frameObj = FrameObj;
    })(window);
});