<template>
  <div>
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button @click="exportTable" v-text="t('reports.productStock.export')"/>
        <a-button @click="primaryPrint" type="primary" v-text="t('reports.productStock.regularPrint')"/>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'id'">
          <a @click="handleStockFlow(record)"> {{ t('reports.productStock.table.flow') }} </a>
        </template>
      </template>
    </BasicTable>
    <StockFlowModal @register="registerModal"/>
  </div>
</template>
<div>
</div>

<script lang="ts">
import {defineComponent, ref} from "vue";
import {BasicTable, TableAction, useTable} from "@/components/Table";
import {useModal} from "@/components/Modal";
import {productStockColumns, searchProductStockSchema} from "@/views/report/report.data";
import {Tag} from "ant-design-vue";
import {getProductStock, exportProductStock} from "@/api/report/report";
import XEUtils from "xe-utils";
import StockFlowModal from "@/views/report/modal/StockFlowModal.vue";
import printJS from "print-js";
import {useMessage} from "@/hooks/web/useMessage";
import {getTimestamp} from "@/utils/dateUtil";
import {useI18n} from "vue-i18n";
import {useLocaleStore} from "@/store/modules/locale";
export default defineComponent({
  name: 'ProductStock',
  components: {Tag, TableAction, BasicTable, StockFlowModal},
  setup() {
    const { t } = useI18n();
    const [registerModal, {openModal}] = useModal();
    const printTableData = ref<any[]>([]);
    const { createMessage } = useMessage();
    const printTotalInitStock = ref(0);
    const printTotalCurrentStock = ref(0);
    const printTotalStockAmount = ref(0);
    const amountSymbol = ref<string>('')
    const localeStore = useLocaleStore().getLocale;
    if(localeStore === 'zh_CN') {
      amountSymbol.value = '￥'
    } else if (localeStore === 'en') {
      amountSymbol.value = '$'
    }
    const [registerTable, { reload, getForm, getDataSource }] = useTable({
      title: t('reports.productStock.title'),
      api: getProductStock,
      rowKey: 'id',
      columns: productStockColumns,
      formConfig: {
        labelWidth: 110,
        schemas: searchProductStockSchema,
        autoSubmitOnEnter: true,
      },
      bordered: true,
      useSearchForm: true,
      showTableSetting: true,
      striped: true,
      canResize: false,
      showIndexColumn: true,
      showSummary: true,
      summaryFunc: handleSummary,
    });

    function handleSummary(tableData: Recordable[]) {
      const totalInitStock = tableData.reduce((prev, next) => prev + next.initialStock, 0);
      const totalCurrentStock = tableData.reduce((prev, next) => prev + next.currentStock, 0);
      const totalStockAmount = tableData.reduce((prev, next) => prev + next.stockAmount, 0);
      // 将数据写入到printTableData里面
      printTotalInitStock.value = totalInitStock;
      printTotalCurrentStock.value = totalCurrentStock;
      printTotalStockAmount.value = totalStockAmount;
      printTableData.value = tableData;
      return [
        {
          _index: 'Total',
          initialStock: totalInitStock,
          currentStock: totalCurrentStock,
          stockAmount: amountSymbol.value + `${XEUtils.commafy(XEUtils.toNumber(totalStockAmount), { digits: 2 })}`
        },
      ];
    }

    function handleStockFlow(record: Recordable) {
      openModal(true, record);
    }

    async function handleSuccess() {
      reload();
    }

    async function handleCancel() {
      reload();
    }

    function exportTable() {
      if (getDataSource() === undefined || getDataSource().length === 0) {
        createMessage.warn(t('reports.productStock.table.notice'));
        return;
      }
      const data: any = getForm().getFieldsValue();
      exportProductStock(data).then(res => {
        const file: any = res;
        if (file.size > 0) {
          const blob = new Blob([file]);
          const link = document.createElement("a");
          link.href = URL.createObjectURL(blob);
          const timestamp = getTimestamp(new Date());
          link.download = t('reports.productStock.data') + timestamp + ".xlsx";
          link.target = "_blank";
          link.click();
        }
      });
    }

    function primaryPrint() {
      const printColumns = productStockColumns.filter(item => item.dataIndex !== 'productId' && item.dataIndex !== 'warehouseId'
          && item.dataIndex !== 'id');
      printTableData.value.push({
        initialStock: printTotalInitStock.value,
        currentStock: printTotalCurrentStock.value,
        stockAmount: amountSymbol.value + `${XEUtils.commafy(XEUtils.toNumber(printTotalStockAmount.value), { digits: 2 })}`,
        productBarcode: 'Total',
        // 将其他字段写成空字符串 为了打印的时候不显示
        warehouseName: '',
        productName: '',
        productCategoryName: '',
        productStandard: '',
        productModel: '',
        productWeight: '',
        productColor: '',
        productUnit: '',
        warehouseShelves: '',
        unitPrice: ''
      });

      printJS({
        documentTitle: "EAIRP " + t('reports.productStock.detail'),
        properties: printColumns.map(item => {
          return { field: item.dataIndex, displayName: item.title }
        }),
        printable: printTableData.value,
        gridHeaderStyle: 'border: 1px solid #ddd; font-size: 12px; text-align: center; padding: 8px;',
        gridStyle: 'border: 1px solid #ddd; font-size: 12px; text-align: center; padding: 8px;',
        type: 'json',
      });

      // 移除最后一条数据
      printTableData.value.pop();
    }

    return {
      t,
      registerTable,
      handleSuccess,
      handleCancel,
      registerModal,
      handleStockFlow,
      exportTable,
      primaryPrint
    }
  }
})
</script>