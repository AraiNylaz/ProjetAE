
const htmlDecode = (value) => {
    return $('<div/>').html(value).text();
}
export default htmlDecode;
