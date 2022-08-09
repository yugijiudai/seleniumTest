// $(function () {
    (function (obj) {

        class DomObj {
            /**
             * 根据自定义属性获取对应的dom
             * @param tagName 自定义属性的dom所在的tag名字
             * @param attr 自定义的属性名字
             * @returns {Array} 返回找到的元素列表
             */
            static getCustomAttributeDom(tagName, attr) {
                let tagNames = document.getElementsByTagName(tagName);
                let result = [];
                for (let i = 0, len = tagNames.length; i < len; i++) {
                    let node = tagNames[i];
                    if (node.getAttributeNode(attr)) {
                        result.push(node);
                    }
                }
                return result;
            }

            /**
             * 让某个checkbox选中,如果没有选中则选中
             * @param jqDom 需要操作的dom
             */
            static checkTheBox(jqDom) {
                if (!jqDom.prop('checked')) {
                    jqDom.prop('checked', true);
                }
            }

            /**
             * 根据传进来的字符串类型的脚本获取dom
             * @param script 字符串类型的获取dom脚本
             * @returns 返回对应的dom
             */
            static getDomByScript(script) {
                return eval(script);
            }


            /**
             * 校验dom是否存在
             * @param script 查找dom的脚本
             * @returns {boolean} true表示存在
             */
            static checkDomExist(script) {
                let dom = this.getDomByScript(script);
                return dom !== null && dom !== undefined;
            }

            /**
             * 利用xpath的方式校验dom是否存在
             * @param script xpath的脚本
             * @returns {boolean} true表示存在
             */
            static checkXpathDomExist(script) {
                let dom = this.getDomByXpath(script);
                return dom !== null && dom !== undefined;
            }

            /**
             * 校验jq的dom是否存在
             * @param script 查找dom的脚本
             * @returns {boolean} true表示存在
             */
            static checkJqDomExist(script) {
                let dom = this.getDomByScript(script);
                return dom.length > 0;
            }

            /**
             * 根据提供的jq脚本查询这个对应的value
             * @param jqDom jq的dom脚本
             * @returns {*|string|undefined}
             */
            static getDomValue(jqDom) {
                let dom = this.getDomByScript(jqDom);
                return dom.val();
            }

            /**
             * 根据xpath获取dom
             * @param xpath 对应的xpath
             * @returns {Node}
             */
            static getDomByXpath(xpath) {
                return document.evaluate(xpath, document).iterateNext();
            }

            /**
             * 使用定时任务的方式轮询指定的脚本，直到条件满足或者超时为止
             * @param maxWaitTime 最大的等待时间
             * @param intervalTime 定时任务的轮询时间
             * @param script 条件结束的js脚本
             */
            static waitDomByTimer(script, maxWaitTime, intervalTime) {
                return new Promise(resolve => {
                    let endTime = new Date().getTime() + maxWaitTime;
                    let timer = setInterval(() => {
                        setTimeout(() => {
                            try {
                                let dom = new Function(script)();
                                console.log(`执行脚本:${script}`)
                                console.log(`执行结果:${dom}`)
                                if (dom) {
                                    clearTimeout(timer);
                                    resolve("true");
                                }
                                if (new Date().getTime() > endTime) {
                                    throw Error(`脚本:【${script}】超时无法找到`);
                                }
                            } catch (err) {
                                clearTimeout(timer);
                                resolve(err.message);
                            }
                        }, 0);
                    }, intervalTime);
                });
            }

            /**
             * 获取waitDomByTimer的结果
             * @param maxWaitTime 最大的等待时间
             * @param intervalTime 定时任务的轮询时间
             * @param script 条件结束的js脚本
             */
            static async getWaitDomByTimerResult(script, maxWaitTime, intervalTime) {
                return await this.waitDomByTimer(script, maxWaitTime, intervalTime);
            }

        }

        obj.domHelper = obj.domHelper || {};
        obj.domHelper.domObj = DomObj;
    })(window);
// });