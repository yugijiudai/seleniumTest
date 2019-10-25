$(function () {
    (function (obj) {
        class BizObj {

            static getTheTableData(tableDom) {
                let result = [];
                let table = domHelper.domObj.getDomByScript(tableDom);
                let trs = table.find('tr');
                trs.each((idx, item) => {
                    let data = $(item).find('td').eq(1).text().trim();
                    result.push(data);
                });
                return result;
            }


        }

        obj.bizHelper = obj.bizHelper || {};
        obj.bizHelper.bizObj = BizObj;
    })(window);
});